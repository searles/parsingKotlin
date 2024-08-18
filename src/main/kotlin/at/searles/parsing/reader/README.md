# Design decisions

`CodePointReader` should be as simple as possible. Additional functionality
should be achieved via Wrappers.

Possibilities:

## PositionCodePointReader

Here, an additional index is used. 
* Pros: 
  * Easy.
  * Reflects the iterative nature of a lexer
* Cons: 
  * Too much freedom for `index`
  * Difficult to create an internal 'frame(from, to)' or mark items when parsing.
  * Does not reflect recursive nature
  * No control which positions are stored and might be needed later.

## StackReader

Maintains an internal stack to which positions are added.

* Pros: Definitely reflects the recursive nature
* Cons: 
  * For a lexer I would still need an index.
  * Even more difficult to create a frame.

## CodePointReader with Checkpoint

* Pros: 
  * Might be a good combination of both
  * A checkpoint instance could serve as a frame.

