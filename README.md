# javascript-externs-generator
Currently a WIP.

Based heavily on http://www.dotnetwise.com/Code/Externs/

## Using the tool
The latest working version can be found here: http://jmmk.github.io/javascript-externs-generator/

## Development
* ```lein cljsbuild auto dev```
* open index.html in your browser
* develop

## TODO
* Clean up UI
* Test on other libraries (currently tested with pixi.js, jQuery, d3, and three.js)
* Fix Errors (See Below)
* Release to public (Reddit, Google groups)

# Tested libraries

## Error
| Library | URL | Problem
|---------|-----|--------
| d3.js   | https://cdn.rawgit.com/mbostock/d3/master/d3.js | ```Uncaught RangeError: Maximum call stack size exceeded```
| three.js| https://cdn.rawgit.com/mrdoob/three.js/master/build/three.js|```Uncaught TypeError: Cannot read property 'order' of undefined```

## Working
| Library | URL
|---------|----
| pixi.js | https://cdn.rawgit.com/GoodBoyDigital/pixi.js/master/bin/pixi.js
| jQuery  | http://code.jquery.com/jquery-2.1.4.js, http://code.jquery.com/jquery-1.9.1.js
