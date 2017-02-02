# javascript-externs-generator
Try it out: http://jmmk.github.io/javascript-externs-generator/

**Note from the developer**:
The externs generator works pretty well for a wide selection of JS libraries, but there are many exceptions (see the issues for examples). I try to respond to and diagnose issues and I am glad to accept Pull Requests, but I am not actively using or maintaining this.

If you find that it couldn't properly generate externs, I would advise two things:

1. If you only call a few functions from the library, write the extern by hand
2. Check out [David Nolen](https://github.com/swannodette)'s work on "Externs Inference":
    - [2016-12-13 Initial Details](https://gist.github.com/swannodette/4fc9ccc13f62c66456daf19c47692799)
    - [2017-01-27 Announcement on ClojureScript Google Group](https://groups.google.com/forum/#!topic/clojurescript/wGJeG36GPTk)
    - [Official Docs for JS Modules](https://clojurescript.org/guides/javascript-modules)
    - [Official Docs for JS Externs](https://clojurescript.org/guides/externs)

## What is it?
This is a tool that generates an extern file detailing an object's properties, methods, and prototype. It's specifically meant for use with the Google Closure Compiler, which needs any variables defined outside of your code to be declared so that it won't rename or remove them.

For a more in-depth explanation of Google Closure Compiler advanced compilation and externs, see the [Google Documentation](https://developers.google.com/closure/compiler/docs/api-tutorial3).

## How to use it
#### Web UI (Recommended)
* Go to http://jmmk.github.io/javascript-externs-generator/
* Load one or more JS files
* Enter the main namespace to extern
* Generate the extern

#### Node CLI (For scripting/automation)
Install the cli script: `npm install -g externs-generator`

##### Basic Usage
* Make sure the JS library you want to extern is available locally: `curl http://code.jquery.com/jquery-1.9.1.js -o jquery.js`
* Run the script: `generate-extern -f jquery.js -n jQuery -o jquery-extern.js`

##### Advanced Usage - Load multiple files
* Make sure all JS libraries are available locally:
 * `curl -O https://cdnjs.cloudflare.com/ajax/libs/react/15.0.2/react.js`
 * `curl -O https://cdnjs.cloudflare.com/ajax/libs/react/15.0.2/react-dom.js`
 * `curl -O http://cdn.jsdelivr.net/webjars/org.webjars.npm/react-relay/0.8.0/dist/relay.js`
* Run the script: `generate-extern -f react.js,react-dom.js,relay.js -n Relay -o relay-extern.js`

##### Known Issues
* Some libraries may not be externed properly from the command line - possibly due to differences in jsdom and a real browser environment. The only example I've found is PIXI.js, but it's likely there are more.

## How does it work?
The strategy can be broken into three steps:

1. Evaluate the code - we want to inspect the runtime representation of the object
2. Recursively walk through the properties of the object and build a tree of all its properties and metadata about those properties
3. Recursively walk through the tree and build a string representation of each property

## Development
#### Web UI
* `lein figwheel dev`
* open index.html in your browser
* develop

#### Node CLI
* `npm install`
* `lein cljsbuild auto cli`
* develop
* test: `./bin/extern -f <library>.js -n <library-name> -o extern.js`
* If you ever clean/remove ./bin/extern, you will need to run `chmod +x ./bin/extern` when it is recreated

## Run Unit Tests
* Make sure phantomjs is installed (follow instructions at https://github.com/bensu/doo#setting-up-environments)
* `lein doo phantom test`

## Issues
Please report any issues to https://github.com/jmmk/javascript-externs-generator/issues

## Credits
Based on http://www.dotnetwise.com/Code/Externs/
