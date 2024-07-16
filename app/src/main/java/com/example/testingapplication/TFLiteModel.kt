import android.content.Context
import com.example.testingapplication.Tokenizer
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

class TFLiteModel(context: Context) {
    private var interpreter: Interpreter
    private val tokenizer: Tokenizer

    init {
        tokenizer = Tokenizer(context) // Initialize Tokenizer directly with context
        interpreter = Interpreter(loadModelFile(context))
    }

    @Throws(IOException::class)
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun runInference(inputIds: IntArray, attentionMask: IntArray, tokenTypeIds: IntArray): Array<FloatArray> {
        val batchSize = 1 // Assuming a batch size of 1 for simplicity
        val inputShape = inputIds.size // Assuming all input arrays are the same length

        // Reshape inputs to include the batch size
        val reshapedInputIds = Array(batchSize) { inputIds }
        val reshapedAttentionMask = Array(batchSize) { attentionMask }
        val reshapedTokenTypeIds = Array(batchSize) { tokenTypeIds }

        // Combine reshaped arrays into one object
        val inputs: Array<Any> = arrayOf(reshapedInputIds, reshapedAttentionMask, reshapedTokenTypeIds)

        // Prepare output array to collect the model output
        // Adjusting the output shape to match the expected [1, 4, 384] from the model
        val outputData = Array(batchSize) { Array(inputShape) { FloatArray(384) } }

        // Run the model
        interpreter.runForMultipleInputsOutputs(inputs, mapOf(0 to outputData))
        println("Running inference with inputs:")
        println("inputIds: ${inputIds.joinToString()}")
        println("attentionMask: ${attentionMask.joinToString()}")
        println("tokenTypeIds: ${tokenTypeIds.joinToString()}")
        println("Inference output: ${outputData.contentDeepToString()}")

        // Return the first result of the output batch
        // Now returning a 2D array since each output is an array of FloatArray
        return outputData[0]
    }


    fun calculateSimilarity(text1: String, text2: String): Float {
        val inputIds1 = preprocess(text1)
        val inputIds2 = preprocess(text2)

        val attentionMask1 = IntArray(inputIds1.size) { 1 }
        val attentionMask2 = IntArray(inputIds2.size) { 1 }

        val tokenTypeIds1 = IntArray(inputIds1.size) { 0 }
        val tokenTypeIds2 = IntArray(inputIds2.size) { 0 }

        val embeddings1 = runInference(inputIds1, attentionMask1, tokenTypeIds1)
        val embeddings2 = runInference(inputIds2, attentionMask2, tokenTypeIds2)

        println("Embeddings for '$text1': ${embeddings1.contentDeepToString()}")
        println("Embeddings for '$text2': ${embeddings2.contentDeepToString()}")

        println("Token IDs for '$text1': ${inputIds1.joinToString()}")
        println("Token IDs for '$text2': ${inputIds2.joinToString()}")

        return calculateAverageCosineSimilarity(embeddings1, embeddings2)
    }

    private fun calculateAverageCosineSimilarity(embeddings1: Array<FloatArray>, embeddings2: Array<FloatArray>): Float {
        // Use the minimum length of the two embeddings arrays
        val minLength = minOf(embeddings1.size, embeddings2.size)

        var totalSimilarity = 0.0f
        for (i in 0 until minLength) {
            totalSimilarity += cosineSimilarity(embeddings1[i], embeddings2[i])
        }
        return totalSimilarity / minLength
    }

    private fun preprocess(text: String): IntArray {
        return tokenizer.encode(text, true)
    }

    private fun cosineSimilarity(vectorA: FloatArray, vectorB: FloatArray): Float {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0

        for (i in vectorA.indices) {
            dotProduct += (vectorA[i] * vectorB[i])
            normA += (vectorA[i] * vectorA[i])
            normB += (vectorB[i] * vectorB[i])
        }

        return (dotProduct / (sqrt(normA) * sqrt(normB))).toFloat()
    }

    // Method to get embeddings for a text
    fun getEmbeddings(text: String): Array<FloatArray> {
        val inputIds = preprocess(text)
        val attentionMask = IntArray(inputIds.size) { 1 }
        val tokenTypeIds = IntArray(inputIds.size) { 0 }

        return runInference(inputIds, attentionMask, tokenTypeIds)
    }

    // Assuming the Tokenizer has methods to get details about it, expose those details
    fun tokenizerDetails(): String {
        return tokenizer.toString()  // Customize this based on actual methods available in Tokenizer
    }

    fun preprocessAndPrintDetails(text: String): IntArray {
        val encoded = tokenizer.encode(text, true)
        println("Encoded tokens for '$text': ${encoded.joinToString()}")
        return encoded
    }

}
