# Unofficial D2Lang confluence macro plugin

This plugin adds a new `d2` macro to your confluence installation that allows you to embed d2 diagrams in your confluence pages.


## Installation

Unfortunately, the app/plugin is not public on the Atlassian Marketplace, so you'll have to manually install it.

1. Download the latest release JAR
2. Install it manually in your confluence instance
3. Start using the new `d2` macro

## Usage

1. Create a new page
2. Add a new macro by typing `{d2` + ENTER
3. Enter your d2lang specification for a diagram
4. Publish the page
5. Upon publishing, the diagram (an svg) will be generated and displayed on the page

## Example

D2 input:

```
direction: right

Web Application -> Backend.Service A -> Oracle DB
Web Application -> Backend.Service B -> SQLite
```

SVG output:

![Example diagram](./example.svg)

## Important confluence plugin development info

### Creating a new plugin

`atlas-create-confluence-plugin`

### Running a local confluence instance

`atlas-run`

### Local dev cycle

1. `atlas-run`
2. `atlas-mvn package`

Confluence has a quick reload feature so can be left running. It'll automatically reload the latest changes.

### Here are the SDK commands you'll use immediately:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

