# [ISSigKeys]

[:octicons-link-external-16: Inno Setup Reference](https://jrsoftware.org/ishelp/index.php?topic=issigkeyssection){
.md-button .md-button--primary }

The `[ISSigKeys]` section defines public keys used to verify `.issig` file signatures. These keys are referenced by the
`ISSigAllowedKeys` parameter and the `issigverify` flag in `[Files]`.

*This section is available since Inno Setup 6.5.*

---

## Name

`string` · **Required**

Identifier for this key entry. `[Files]` entries refer to it through `ISSigAllowedKeys`.

---

## Group

`string`

Logical group name allowing multiple keys to share an identifier in `ISSigAllowedKeys`.

---

## KeyFile

`string`

Path to a key file containing public key data.

---

## PublicX

`string`

Hex-encoded X coordinate of the public EC key.

---

## PublicY

`string`

Hex-encoded Y coordinate of the public EC key.

---

## KeyID

`string`

Optional compile-time key identifier embedded in the installer for key lookup.

---

## RuntimeID

`string`

Optional runtime key identifier used by the installer at install time for key lookup.

