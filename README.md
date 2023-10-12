# Seamless Auth
Seamless Auth is a mod for Minecraft 1.7.10 that modifies the login handshake for offline mode servers to allow for public key based authentication.
Additionally, it allows clients to upload skin textures directly to the server, allowing users to have skins and capes without accessing Microsoft authentication servers.

## Usage
Unimixins is required for this mod to work.
Install the mod's jar into the Forge mods directory on both server and client, both sides are required for authentication to work, but a client with the mod can connect to servers without it. Just installation is sufficient for basic usage, but further configuration allows for secure whitelisting and shared keys between servers.
Users may place a `skin.png` and/or `cape.png` into the instance directory (by default, configurable), any server with skin sharing enabled will then give other clients that texture to display as the user's skin and/or cape.
### Configuration
See `seamlessauth.cfg` in Forge's configuration directory, the mod must run at least once for this file to be generated.
### Commands
The command `/seamlessauth reload-skin` will reload the configuration file and skin textures, and send a message to the server with the new hashes. This allows users to hot-reload their skin while connected to a server.

## Contributing
You're more likely to have your request noticed if you post the patch to /mmcg/ than using any web interface. Treat Codeberg/Github/whatever as mirrors only.