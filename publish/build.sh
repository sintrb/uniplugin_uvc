ID="sintrb-uvc"
VER="1.0.0"
echo "build $ID@$VER ..."
[ -f $VER ] || mkdir $VER
[ -f tmp ] || mkdir tmp
[ -f versions ] || mkdir versions


rm -rf $VER/*

echo "copy demo..."
cp -r "/Users/robin/Downloads/Android-SDK@3.4.6.81299_20220418/UniPlugin-Hello-AS/uniapp示例工程源码/unipluginDemo" $VER/$ID-demo
rm -rf $VER/$ID-demo/unpackage
rm -rf $VER/$ID-demo/.hbuilderx
echo "copy done"

mkdir $VER/$ID
mkdir $VER/$ID/android
mkdir $VER/$ID/android/libs

cp ../build/outputs/aar/* $VER/$ID/android/
cp /Users/robin/Downloads/Android-SDK@3.4.6.81299_20220418/UniPlugin-Hello-AS/uniplugin_iutils/build/outputs/aar/* $VER/$ID/android/
cp -rf ../libs/* $VER/$ID/android/libs/
cp package.json $VER/$ID/
cp README.md $VER/

cd $VER/
zip -r ../tmp/$ID-$VER.zip $ID
zip -r ../tmp/$ID-$VER-demo.zip $ID-demo
cd ..

zip -r $VER versions/$ID-$VER.zip

rm -rf /Users/robin/DO/VUE/iplant/nativeplugins/$ID
cp -rf $VER/$ID /Users/robin/DO/VUE/iplant/nativeplugins/$ID

# /Users/robin/Downloads/Android-SDK@3.4.6.81299_20220418/UniPlugin-Hello-AS/uniplugin_uvc/build/outputs/aar/uniplugin_uvc-release.aar