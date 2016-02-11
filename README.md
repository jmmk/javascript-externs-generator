# javascript-externs-generator
Try it out: http://jmmk.github.io/javascript-externs-generator/

## What is it?
This is a tool that generates an extern file detailing an object's properties, methods, and prototype. It's specifically meant for use with the Google Closure Compiler, which needs any variables defined outside of your code to be declared so that it won't rename or remove them.

For a more in-depth explanation of Google Closure Compiler advanced compilation and externs, see the [Google Documentation](https://developers.google.com/closure/compiler/docs/api-tutorial3).

## How does it work?
The strategy can be broken into three steps:

1. Evaluate the code - we want to inspect the runtime representation of the object
2. Recursively walk through the properties of the object and build a tree of all its properties and metadata about those properties
3. Recursively walk through the tree and build a string representation of each property

## Development
* `lein figwheel dev`
* open index.html in your browser
* develop

## Run Unit Tests
* Make sure node is installed (follow instructions at https://github.com/bensu/doo#node)
* `lein doo node test`

## TODO
* Test on other libraries (currently tested with pixi.js, jQuery, d3, ZeroClipboard, and three.js)
* Fix Errors (See Below)

# Tested libraries

## Error
| Library | URL | Problem
|---------|-----|--------
| three.js| https://cdnjs.cloudflare.com/ajax/libs/three.js/r71/three.js|`Uncaught TypeError: Cannot read property 'order' of undefined`

## Working
Note: Working means that an extern was successfully generated, but it might still be incomplete

| Library | URL
|---------|----
| pixi.js | https://cdnjs.cloudflare.com/ajax/libs/pixi.js/3.0.6/pixi.js
| d3.js   | https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js
| jQuery  | http://code.jquery.com/jquery-2.1.4.js, http://code.jquery.com/jquery-1.9.1.js
| ZeroClipboard | https://cdnjs.cloudflare.com/ajax/libs/zeroclipboard/2.2.0/ZeroClipboard.js

## Credits
Based on http://www.dotnetwise.com/Code/Externs/
