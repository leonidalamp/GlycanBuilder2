# GlycanBuilder2
<!--
書いておくこと（書き終わったものは文頭に"*"を書いておく）

* 実行ファイルへのリンク（RINGSで公開されているもの, GLICで公開されているとのこと）
  64bitOSで実行することを想定して良さそう、32bitは考慮しない

* ビルドのやり方
* 実行ファイルの実行方法
* 論文の書誌情報
  マニュアルはどこかにあっただろうか？

* GUIの使用する場合の操作方法（必要最低限の操作は書いておいたほうがいいかもしれない）
* Import/Exportのやり方だけでも書いておく方がユーザビリティに富むと思われるが
-->

## Downloads

### Standalone executable GlycanBuilder2
* [GlycanBuilder2](https://gitlab.com/GlycoTool/dev-version/-/tree/master/GlycanBuilder)
* Supported OS : 
  * Windowds (64bit)
  * macOS (64bit/Intel&M1,M2)


## LDA Java 21 distribution

This branch builds the light GlycanBuilder distribution embedded by Lipid Data
Analyzer 2. It retains the editor, GWS/WURCS import and export, LDA shorthand
conversion, fragmentation, rendering, and raster/SVG export workflows used by
LDA. Legacy GlycoCT parsers, PDF/PostScript export, NativeSwing/SWT integration,
JGoodies, and Ant launcher dependencies are excluded because LDA does not use
them and their transitive libraries conflict with the Java 21 module system.

## Requirement
* Java 21
* Maven 3.9 (or later)

Before building, run `mvn -version` and verify that Maven reports Java 21.
Setting only the Eclipse execution environment is not sufficient; Maven uses
the JDK selected by `JAVA_HOME`.

## Release node
Latest version: 1.25.0\\
Please see about [details](CHANGELOG.md).

## Compile
Clone this repository in the local repository.
```
git clone https://github.com/glycoinfo/GlycanBuilder2.git
```

Move to the cloned local repository and compile the source files
```
cd ~/Directory_of_local_repository/GlycanBuilder2
```
```
mvn clean compile
```

## JAR file
Runable JAR (Java Archive) file is generate the below command.
```
mvn clean package -Pmake-fat-jar
```

When compilation in finished, jar file is created in the target folder.
>[INFO] Building jar: /../../Directory_of_local_repository/GlycanBuilder2/target/glycanbuilder2-lda-java21.jar

- Windows or Linux
```
java -jar ./target/glycanbuilder2-lda-java21.jar
```
- Mac OS X
```
java -XstartOnFirstThread -jar ./target/glycanbuilder2-lda-java21.jar
```
## Example

### Import WURCS string

![Imgur](https://i.imgur.com/6RcNetX.png)
1. Click **Add structure from string** (Red marked).
2. Paste WURCS string into the text area, and select **WURCS2** in the **input sequence format** (Red marked).
3. When click **import** button, represent glycan image on the canvas.

### Export WURCS string

![Import](https://i.imgur.com/6eQ1qkb.png)
1. Drag and select a glycan image on the canvas.
2. Click **Get string from structure** (Red marked).
3. Select **WURCS2** in the **String encoded**.
4. WURCS2 string is output.

### Export images

![Image](https://i.imgur.com/XXmnrdg.png)
1. Drag and select a glycan image on the canvas.
2. Click **Export to graphical formats** on the **File** tab and select the image format.
3. Select a directory to save the image.

## Publications
* [Shinichiro Tsuchiya, Nobuyuki P. Aoki, Daisuke Shinmachi, Masaaki Matsubara, Issaku Yamada, Kiyoko F. Aoki-Kinoshita, Hisashi Narimatsu,
Implementation of GlycanBuilder to draw a wide variety of ambiguous glycans, Carbohydrate Research, Volume 445, 2017, Pages 104-116](https://www.sciencedirect.com/science/article/pii/S0008621516305316)
