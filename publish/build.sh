ID="sintrb-uvc"
VER="1.0.6"

BASHPATH=$(cd `dirname $0`; pwd)
echo $BASHPATH
cd $BASHPATH

echo "build $ID@$VER ..."
[ -f tmp ] || mkdir tmp
[ -f tmp/$VER ] || mkdir tmp/$VER
[ -f versions ] || mkdir versions


rm -rf tmp/$VER/*

echo "copy demo..."
cp -r /Users/robin/Downloads/Android-SDK@3.4.6.81299_20220418/UniPlugin-Hello-AS/uniplugin_uvc/publish/uvc-demo tmp/$VER/$ID-demo
rm -rf tmp/$VER/$ID-demo/unpackage
rm -rf tmp/$VER/$ID-demo/.hbuilderx
echo "copy done"

mkdir tmp/$VER/$ID
mkdir tmp/$VER/$ID/android
mkdir tmp/$VER/$ID/android/libs

cp ../build/outputs/aar/*-release.aar tmp/$VER/$ID/android/
cp /Users/robin/Downloads/Android-SDK@3.4.6.81299_20220418/UniPlugin-Hello-AS/uniplugin_iutils/build/outputs/aar/*-release.aar tmp/$VER/$ID/android/
cp -rf ../libs/* tmp/$VER/$ID/android/libs/
cp package.json tmp/$VER/$ID/
cp README.md tmp/$VER/
rm -rf tmp/$VER/$ID-demo/unpackage
rm -rf tmp/$VER/$ID-demo/nativeplugins
# nativePlugins
cat tmp/$VER/$ID-demo/manifest.json | sed 's/nativePlugins/xxxxx/' > /tmp/$VER-$ID-demo-manifest.json
mv /tmp/$VER-$ID-demo-manifest.json tmp/$VER/$ID-demo/manifest.json

cd tmp
rm -rf ../versions/$ID-$VER-all.zip
zip -r ../versions/$ID-$VER-all.zip $VER
cd ..

rm -rf /Users/robin/DO/VUE/iplant/nativeplugins/$ID
cp -rf tmp/$VER/$ID /Users/robin/DO/VUE/iplant/nativeplugins/$ID

cd tmp/$VER/
zip -r $ID-$VER.zip $ID
zip -r $ID-$VER-demo.zip $ID-demo
cd ../../

# /Users/robin/Downloads/Android-SDK@3.4.6.81299_20220418/UniPlugin-Hello-AS/uniplugin_uvc/build/outputs/aar/uniplugin_uvc-release.aar