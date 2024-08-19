<h1 style="display: flex; align-items: center; justify-content: space-around;"><img height="100em" src="./src/main/resources/assets/beaconwaypoints/icon.png" alt="icon"> Beacon Waypoints</h1>

![Modrinth Downloads](https://img.shields.io/modrinth/dt/bRwhijps?style=for-the-badge&logo=Modrinth&label=Modrinth%20Downloads&color=00af5c&link=https%3A%2F%2Fmodrinth.com%2Fmod%2Fbeacon-waypoints) ![CurseForge Downloads](https://img.shields.io/curseforge/dt/1087273?style=for-the-badge&logo=curseforge&label=CurseForge%20Downloads)
 ![GitHub Release](https://img.shields.io/github/v/release/Adrien5902/beaconwaypoints?style=for-the-badge&label=Latest%20Released%20Version)

A simple server-sided mod (works on singleplayer when on client) to turn beacon into waypoints which you can teleport to.


Required mods : [Fabric API](https://modrinth.com/mod/fabric-api)

## How to use

Right click with a renamed pearl on an activated beacon to enable teleportation to it.

Then use the /warp command to teleport where you want

*Either specify a waypoint name or no argument to open the menu*

> [!TIP]
> Throw an item on top of the beacon when creating it to change the icon displayed in the menu

## Technical Details

The waypoints data is stored under the `%appdata%/.minecraft/saves/world/data/beaconwaypoints.data file`
