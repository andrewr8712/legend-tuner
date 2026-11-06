# Local signing key

This directory is where a maintainer can keep a **local** debug/release key. The actual keystore and its passwords are intentionally excluded from git.

The public Android CI build does not need this directory: Gradle falls back to its standard machine-generated debug key. To reproduce a specific signed build locally, create a key and a matching `keystore.properties` in the repository root:

```bash
keytool -genkeypair \
  -keystore keystore/debug.jks \
  -storepass "choose-a-password" \
  -keypass "choose-a-password" \
  -alias legendtuner \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=Config Tuner, O=Config Tuner"
```

Then create `keystore.properties` (ignored by git):

```properties
storeFile=keystore/debug.jks
storePassword=choose-a-password
keyAlias=legendtuner
keyPassword=choose-a-password
```

Keep this key safe: Android only installs an update over an existing app when the package name and signing certificate match.

The official APK attached to a GitHub Release is built from the maintainer's local key and is therefore not reproducible from a clean public checkout.

## Public release key

The debug key above is for local debug builds only. Public APKs use a separate,
dedicated release key:

```bash
keytool -genkeypair \
  -keystore keystore/config-tuner-release.jks \
  -alias config-tuner \
  -keyalg RSA -keysize 4096 -validity 10000 \
  -storetype PKCS12
```

Put its credentials in a root-level `release-keystore.properties` (ignored by
git, mode 600):

```properties
storeFile=keystore/config-tuner-release.jks
storePassword=...
keyAlias=config-tuner
keyPassword=...
```

`app/build.gradle.kts` reads this file only when it exists and assigns its
`releaseKey` signing config to the release build type. Debug builds continue to
use `keystore.properties` and the debug key. Never commit either properties file
or any `.jks` file. Back up the release keystore and its password offline; losing
them means future updates to `dev.legendtuner.app` cannot be installed over the
existing app.
