**EMMAITAR**

Emmaitar is a craftable custom paintings mod for Minecraft 1.7.10 which I developed for use alongside the LOTR mod.

When installed on a server, the admins can upload painting images and configure them to be available in-game with specific recipes. Crucially, the client **does not need to have Emmaitar installed to connect** - however, without it, the custom paintings will not show up in-game.

*Emmaitar* is Quenya for 'picture-artist' - *painter*.

**Mod Download** - Emmaitar 1.0 (to be added)


**How do I configure the paintings?**

The mod generates a server directory, `emmaitar-paintings`, into which you place the painting images. A painting must be a `.png` file, and the width and height must be multiples of 16.

Each painting must have an associated metadata file of the same name as the `.png`. These are plain text files with a special extension, which is `.epm` (**E**mmaitar **P**ainting **M**etadata).

In this metadata file you specify:
- the author name;
- the painting's title (different from the filename);
- the in-game dimensions of the painting (width and height, in blocks);
- and a list of dyes used to craft it.

Here is an example of a metadata file: `gondolin.epm` for a painting `gondolin.png`.

```
# The first ever test painting metafile!
#
# Lines beginning with #### are comments and will not be interpreted by the loader.
#
author=Goldbar22
title=The City of Gondolin
w=4
h=4
dyes=LightBlue,LightBlue,LightBlue,White,White,Gray,Green,Gray
```

Lines beginning with `#` are comments, which are ignored. Note the metadata format: `author=Goldbar22`, a key and a value separated by `=`, with **no spaces**. And the key is **case-sensitive** (don't capitalise `Author`).

Most of these properties are self-explanatory, but let's take some time to explain the recipe syntax:


**Recipe syntax**

The recipe format is a list of eight dyes. In-game, this generates a crafting recipe of the form:

```
[Dye 1] [Dye 2] [Dye 3]
[Dye 4] [Painting] [Dye 5]
[Dye 6] [Dye 7] [Dye 8]
```

The painting item in the middle must be a normal painting item, not an Emmaitar custom painting. The dyes use the Forge ore dictionary, so any dye of that colour will work to craft the painting, vanilla or modded.

To configure these recipes in the metadata file you use a comma-separated list of dye names, as above:

`dyes=LightBlue,LightBlue,LightBlue,White,White,Gray,Green,Gray`

Again, these names are case-sensitive and there must be no spaces between them. There must be exactly **8** dyes - no more, no fewer!

The list of dye names:
```
Black
Red
Green
Brown
Blue
Purple
Cyan
LightGray
Gray
Pink
Lime
Yellow
LightBlue
Magenta
Orange
White
```

For the sake of completion, I should point out that **each custom painting needs to have a unique dye recipe**!
