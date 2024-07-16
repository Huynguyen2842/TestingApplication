package com.example.testingapplication

import TFLiteModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testingapplication.ui.theme.TestingApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var tfliteModel: TFLiteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tfliteModel = TFLiteModel(this)

        val text1 = "That is a happy person"
        val text2 = "fuck dog ever"

        // Get embeddings
        val embeddings1 = tfliteModel.getEmbeddings(text1)
        val embeddings2 = tfliteModel.getEmbeddings(text2)

        // Print embeddings to log or console
        println("Embeddings for '$text1': ${embeddings1.contentDeepToString()}")
        println("Embeddings for '$text2': ${embeddings2.contentDeepToString()}")

        // Get tokenizer details
        println("Tokenizer Details: ${tfliteModel.tokenizerDetails()}")

        // Preprocess and print details for a sample text
        val sampleText = "Hello, world!"
        val encodedTokens = tfliteModel.preprocessAndPrintDetails(sampleText)
        println("Encoded tokens for '$sampleText': ${encodedTokens.joinToString()}")


        // Preprocess and print details for a sample text
        val sampleText1 = "Hi there!"
        val encodedTokens1 = tfliteModel.preprocessAndPrintDetails(sampleText1)
        println("Encoded tokens for '$sampleText1': ${encodedTokens1.joinToString()}")
        val similarity = tfliteModel.calculateSimilarity(text1, text2)

        setContent {
            TestingApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name1 = "$text1",
                        name2 = "$text2",
                        result = "Similarity: $similarity",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name1: String,
    name2: String,
    result: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Text 1: $name1\nText 2: $name2\nResult: $result",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestingApplicationTheme {
//        Greeting("Android", "Sample result")
    }
}