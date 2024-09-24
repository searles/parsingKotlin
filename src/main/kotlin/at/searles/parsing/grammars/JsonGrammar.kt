package at.searles.parsing.grammars

object JsonGrammar {
    /* Grammar definition:

    <json> ::= <object> | <array>

    <object> ::= '{' [ <members> ] '}'

    <members> ::= <pair> | <pair> ',' <members>

    <pair> ::= <string> ':' <value>

    <array> ::= '[' [ <elements> ] ']'

    <elements> ::= <value> | <value> ',' <elements>

    <value> ::= <string>
              | <number>
              | <object>
              | <array>
              | 'true'
              | 'false'
              | 'null'

    <string> ::= '"' <char> '"'
    <char> ::= <any character except " or \> | '\\' <escaped_char>

    <escaped_char> ::= '"' | '\' | '/' | 'b' | 'f' | 'n' | 'r' | 't' | 'u' <hex>

    <hex> ::= <hex_digit> <hex_digit> <hex_digit> <hex_digit>

    <hex_digit> ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
                  | 'a' | 'b' | 'c' | 'd' | 'e' | 'f'
                  | 'A' | 'B' | 'C' | 'D' | 'E' | 'F'

    <number> ::= [ '-' ] <digit> <digits>

    <digit> ::= '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
    <digits> ::= <digit> | <digit> <digits>
     */
}