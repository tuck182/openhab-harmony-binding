openhab-harmony-binding
=======================

openHAB Binding for the Logitech Harmony Hub


### Introduction

The Harmony Hub binding is used to enable communication between openHAB and a single Logitech Harmony Hub device. The API exposed by the Harmony Hub is relatively limited, but it does allow for reading the current activity as well as setting the activity and sending device commands.

### Installing

Extract a release zip into the addons folder of an openHAB runtime installation and restart.

### Usage

#### Configuration

The following configuration items are required to be set in openhab.cfg:

	harmonyhub:host=<local ip address of your hub>
	harmonyhub:username=<your logitech username>
	harmonyhub:password=<your logitech password>

#### Bindings

The Harmony binding supports both outgoing and incoming item bindings of the form:

    { harmonyhub="<binding>[ <binding> ...]" }
    
where `<binding>` can be:

##### Current activity (status)

Displays the current activity:

    String Harmony_Activity         "activity [%s]" { harmonyhub="<[currentActivity]" }
    
##### Start activity (command)

Start the specified activity (where activity can either be the activity id or label).

	String HarmonyHubPowerOff       "powerOff"      { harmonyhub=">[start:PowerOff]" }
	String HarmonyHubWatchTV        "watchTV"       { harmonyhub=">[start:Watch TV]" }

##### Press button (command)

Press the specified button on the given device (where device can either be the device id or label).

	String HarmonyHubMute           "mute"          { harmonyhub=">[press:Denon AV Receiver:Mute]" }

#### Actions

The following actions are supported in rules:

##### Press button

	harmonyPressButton(<device>, <command>)

##### Start activity

	harmonyStartActivity(<activity>)

### Shell

The [harmony-java-client](https://github.com/tuck182/harmony-java-client) project on GitHub provides a simple shell for querying a Harmony Hub, and can be used to retrieve the full configuration of the hub to determine ids of available activities and devices as well as device commands (buttons).

### TODO

* Explanation of how to set up project for development (integrate it into an openHAB dev environment)

