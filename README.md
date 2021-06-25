# Server Permission API
It's simple to use, but covering more functionality permission api designed for fabric
and compatible ecosystem. It's created for simplifying the development of mods wanting to
support multiple permission providers without writing multiple interfaces for every one.

## But there was already an api!
Lucko's fabiric permission api while really helpful for fabric ecosystem, it is quite
lacking in functionality. This replacement is additionally compatible with legacy mods 
(not providers!) so old mods will work just fine.

## Current compatibility:
There are no working implementations currently, excluding Vanilla one

## Usage:
You can use static methods from `Permissions` directly or use `get()` to get current provider
instance. However, you can only obtain it after server started. This means you should use `get()`
or redirecting methods to make sure you won't get it too early.