---
name: icons
description: Create new icons for inno setup plugin
---

# Style

If you need a new icon, look at the existing `language/script/src/main/resources/icons/*.svg` icons and reuse their
color, stroke, fill, shape and size for the new icon.

# Usage

1. Create a new SVG file in `src/main/resources/icons/` with a name that reflects the purpose of the icon.
2. Use the existing icons as a reference for color, stroke, fill, shape, size, and other design elements.
3. Ensure that the new icon is consistent with the overall style of the existing icons.
4. Add it to the local `*Icons` class in the root package to use it in the plugin.
