package com.woocommerce.android.ai

object AIPrompts {
    private const val PRODUCT_DESCRIPTION_PROMPT = "Write a description for a product with title \"%1\$s\"%2\$s.\n" +
        "Your response should be in the ISO language code \"%3\$s\". \n" +
        "Make the description 50-60 words or less.\n" +
        "Use a 9th grade reading level.\n" +
        "Perform in-depth keyword research relating to the product in the same language of the product title, " +
        "and use them in your sentences without listing them out."

    fun generateProductDescriptionPrompt(
        name: String,
        features: String = "",
        languageISOCode: String = "en"
    ): String {
        val featuresPart = if (features.isNotEmpty()) " and features: \"$features\"" else ""
        return String.format(PRODUCT_DESCRIPTION_PROMPT, name, featuresPart, languageISOCode)
    }

    private const val PRODUCT_SHARING_PROMPT = "Your task is to help a merchant create a message to share with " +
        "their customers a product named \"%1\$s\". More information about the product:\n" +
        "%2\$s\n" +
        "- Product URL: %3\$s.\n" +
        "Your response should be in the ISO language code \"%4\$s\". \n" +
        "The length should be up to 3 sentences.\n" +
        "Use a 9th grade reading level.\n" +
        "Add related hashtags at the end of the message.\n" +
        "Do not include the URL in the message."

    fun generateProductSharingPrompt(
        name: String,
        url: String,
        description: String = "",
        languageISOCode: String = "en"
    ): String {
        val descriptionPart = if (description.isNotEmpty()) "- Product description: \"$description\"" else ""
        return String.format(PRODUCT_SHARING_PROMPT, name, descriptionPart, url, languageISOCode)
    }

    @Suppress("MaxLineLength", "LongParameterList")
    fun generateProductCreationPrompt(
        name: String,
        keywords: String,
        tone: String,
        weightUnit: String,
        dimensionUnit: String,
        currency: String,
        existingCategories: List<String>,
        existingTags: List<String>,
        languageISOCode: String
    ): String {
        return """
            You are a WooCommerce SEO and marketing expert, perform in-depth research about the product using the provided name, keywords and tone, and give your response in the below JSON format

            name: "$name"
            keywords: "$keywords"
            tone: "$tone"

            Expected json response format:
            "{
               "name":"The name of the product, in the ISO language code "$languageISOCode"",
               "description":"Product description of around 100 words long in a "$tone" tone, in the ISO language code "$languageISOCode"",
               "short_description":"Product's short description, in the ISO language code "$languageISOCode"",
               "virtual":"A boolean value that shows whether the product is virtual or physical",
               "shipping":{
                  "length":"Guess and provide only the number in $dimensionUnit",
                  "weight":"Guess and provide only the number in $weightUnit",
                  "width":"Guess and provide only the number in $dimensionUnit",
                  "height":"Guess and provide only the number in $dimensionUnit"
               },
               "price":"Guess the price in $currency, return the price unformatted",
            ${
                @Suppress("MaxLineLength")
                if (existingTags.isNotEmpty()) """"tags":"Given the list of available tags "${existingTags.joinToString()}", suggest an array of the best matching tags for this product, if no matches are found return an empty array, don’t suggest any value other than the available ones","""
                else ""
            }
            ${
                @Suppress("MaxLineLength")
                if (existingCategories.isNotEmpty()) """"categories":"Given the list of available categories "${existingCategories.joinToString()}", suggest an array of the best matching categories for this product, if no matches are found return an empty array, don’t suggest any value other than the available ones""""
                else ""
            }
            }"
        """.trimIndent()
    }

    private const val LANGUAGE_IDENTIFICATION_PROMPT = "What is the ISO language code of the language used in the " +
        "below text? Do not include any explanations and only provide the ISO language code in your response. \n" +
        "Text: ```(%1\$s)```"

    fun generateLanguageIdentificationPrompt(text: String): String {
        return String.format(LANGUAGE_IDENTIFICATION_PROMPT, text)
    }
}
