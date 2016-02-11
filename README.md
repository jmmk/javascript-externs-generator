# javascript-externs-generator
Try it out: http://jmmk.github.io/javascript-externs-generator/

## What is it?
This is a tool that inspects a JavaScript object in order to generate an extern file detailing the object's properties, methods, and prototype.

For an introduction to Google Closure Compiler advanced compilation and externs, see the [Google Documentation](https://developers.google.com/closure/compiler/docs/api-tutorial3).

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
