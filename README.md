# stonecutter

[![Build Status](https://snap-ci.com/ThoughtWorksInc/stonecutter/branch/master/build_image)](https://snap-ci.com/ThoughtWorksInc/stonecutter/branch/master)

A D-CENT project: an easily deployable oauth server for small organisations.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Development VM

You can also develop and run the application in a VM.  You will need [Vagrant][] installed.

navigate to the ops/ directory of the project and run:

    vagrant up

When the VM has started, access the virtual machine by running:

    vagrant ssh

The source folder will be located at `/var/stonecutter`

After initial setup you will need to run:

    cd /var/stonecutter
    npm install

This will take a while (upwards of 10 minutes).

[Vagrant]: https://www.vagrantup.com

## Running
Before starting the server, build the views by running:

    gulp build

To start a web server for the application in development mode, run:

    lein ring server-headless
    
NB: running the application like this will save users into an in memory cache that will be destroyed as soon as the app is shutdown.

To start a web server with users persisted to mongodb, ensure you have mongo running locally and run:

    lein run

## Running the static frontend

### Getting started

First install [brew](http://brew.sh/)

```
brew install node
npm install
```

You also require gulp to be installed globally.

```
npm install -g gulp 
```

Depending on system privileges you may need to install it globally with sudo:
 
```
sudo npm install -g gulp 
```
 
### Running the prototype

####Simply type
```
gulp server
```

### Running test suite

#### To run all test, run this in your virtual machine
```
lein midje
```

### Customising the app

Optional environment variables can be set up to customise app name, colour scheme and logo.

####App name

App name is used anywhere where the application refers to itself, e.g. "Register with <App name>".
To set the app name:

* Set the environment variable **APP_NAME**

The content of any HTML elements with the class `.clj--app-name` will be replaced with the app name.

####Logo

Logo is used in the header. Maximum dimensions (W x H) 110px x 50px.
To set the logo:

* Set the environment variable **STATIC_RESOURCES_DIR_PATH** to a directory containing the logo.
* **NOTE: Anything inside this directory will be served as a static resource, including subdirectories.**
* Set the environment variable **LOGO_FILE_NAME** to the logo file name including the extension, e.g. logo.png

####Colours

The header colour can be customised:

* Set the environment variable **HEADER_BG_COLOR** to a CSS colour value, e.g. `#1F1F1F` or `"rgb(192,192,192)"`
* Set the environment variable **INACTIVE_TAB_FONT_COLOR** to a CSS colour value.
* The two colours should be contrasting in order for the inactive tab text to be visible.