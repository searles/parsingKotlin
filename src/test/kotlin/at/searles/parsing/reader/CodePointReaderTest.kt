package at.searles.parsing.reader

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayInputStream
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodePointReaderTest {

    companion object {
        const val TEST_STRING_1 =
            "😀 😁 😂 🤣 😃 😄 😅 😆 😉 😊 😋 😎 😍 😘 😗 😙 😚 🥰 😋 🤔 🤭 🤫 🤥 🧐 🤓 😐 😑 😶 😏 " +
            "😒 😬 😔 😪 😴 🙄 🤨 🧐 🤯 🥳 🤠 😇 😈 👿 👹 👺 💀 ☠️ 👻 👽 👾 🤖 🎃 😺 😸 😹 😻 😼 😽 " +
            "🙀 😿 😾 💋 👋 🤚 🖐 ✋ 🖖 👌 ✌ 🤟 🤘 🤙 👈 👉 👆 👇 🖕 ☝ 👍 👎 ✊ 👊 🤛 🤜 👏 🙌 👐 🤲 " +
            "🤝 🙏 💅 🤳 💪 🦾 🦿 🦵 🦶 👂 👃 🧠 🦷 🦴 👀 👁 👅 👄 💋 🩸 💄 💍 💎 👤 👥 🧑 👶 🧒 👦 " +
            "👧 🧑 👱 👨 🧔 👩 🧓 👴 👵 👲 👳 👳‍♀️ 👳‍♂️ 👱‍♀️ 👱‍♂️ 🧑‍⚕️ 👨‍⚕️ 👩‍⚕️ 🧑‍🎓 👨‍🎓 👩‍🎓 🧑‍🏫 👨‍🏫 👩‍🏫 " +
            "🧑‍⚖️ 👨‍⚖️ 👩‍⚖️ 🧑‍🌾 👨‍🌾 👩‍🌾 🧑‍🍳 👨‍🍳 👩‍🍳 🧑‍🔧 👨‍🔧 👩‍🔧 🧑‍🏭 👨‍🏭 👩‍🏭 🧑‍💼 👨‍💼 👩‍💼 🧑‍🔬 👨‍🔬 👩‍🔬 🧑‍💻 👨‍💻 👩‍💻 🧑‍🎤 👨‍🎤 👩‍🎤 " +
            "🧑‍🎨 👨‍🎨 👩‍🎨 🧑‍✈️ 👨‍✈️ 👩‍✈️ 🧑‍🚀 👨‍🚀 👩‍🚀 🧑‍🚒 👨‍🚒 👩‍🚒 👮‍♂️ 👮‍♀️"

        @JvmStatic
        fun codePointReaderFactories(): Stream<(String) -> CodePointReader> = Stream.of(
            { str: String -> StringCodePointReader(str) },
            { str: String -> Utf8CodePointReader(ByteArrayInputStream(str.toByteArray(Charsets.UTF_8)))}
        )
    }

    @DisplayName("Test read method of CodePointReader implementations")
    @ParameterizedTest(name = "{index} => reader={0}")
    @MethodSource("codePointReaderFactories")
    fun `GIVEN a string with code points beyond UTF-16 WHEN CPR is created THEN it returns proper code points`(codePointReaderFactory: (String) -> CodePointReader) {
        // Arrange
        val reader = codePointReaderFactory(TEST_STRING_1)
        TEST_STRING_1.codePoints().forEach { expected ->
            // Act
            val actual = reader.read()

            // Assert
            Assertions.assertEquals(expected, actual)
        }

        // Assert
        Assertions.assertEquals(-1, reader.read())
    }
}