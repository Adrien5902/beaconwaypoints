<h1 style="display: flex; align-items: center; justify-content: space-around;"><img height="100em" src="./src/main/resources/assets/beaconwaypoints/icon.png" alt="icon"> Beacon Waypoints</h1>

A simple server-sided mod (works on singleplayer when on client) to turn beacon into waypoints which you can teleport to.


Required mods : [Fabric API](https://modrinth.com/mod/fabric-api)

| Mod Version | Supported Minecraft Version |
| ----------- | --------------------------- |
| 1.0.0       | 1.21                        |

## How to use

Right click with a renamed pearl on an activated beacon to enable teleportation to it.

Then use the /warp command to teleport where you want

*Either specify a waypoint name or no argument to open the menu*

> [!TIP]
> Throw an item on top of the beacon when creating it to change the icon displayed in the menu

## Technical Details

The waypoints data is stored under the `%appdata%/.minecraft/saves/world/data/beaconwaypoints.data file`