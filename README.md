# KambaQrReader
Lib Android para leitura de QR-Code do Aplicativo Kamba.

[![](https://jitpack.io/v/usekamba/KambaQrReader.svg)](https://jitpack.io/#usekamba/KambaQrReader)

## Baixar
Baixe a ultima versão via Gradle:

**Passo 1.**
Adicione o repositório JitPack para seu arquivo de compilação build.gradle raiz.

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

**Passo 2.**
Adicione a dependência do KambaQrReader

```
dependencies {
  implementation 'com.github.usekamba:KambaQrReader:1.0.1'
}
```

## Usar

**Passo 1.**
Adicione a permissão ao seu arquivo de manifesto

```java
  <uses-permission android:name="android.permission.CAMERA" />
```

**Passo 2.**
Aicione o QrReaderView em sua atividade ou fragmento
```xml
   <com.kamba.qrreader.QrReaderView
        android:id="@+id/KambaQrReaderView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:lineWidth="1dp"
        app:barHeight="1dp"
        app:lineColor="@color/colorAccent"
        app:cornerWidth="12dp"
        app:cornerHeight="3dp" />
``` 

**Passo 3.**
Implemente a interface QrDetectorListener em sua atividade:
```kotlin
class MainActivity : AppCompatActivity(), QrDetectorListener { ...
```
Implemente os membros da interface em sua atividade:
```kotlin
    override fun onDetectQrFromCamera(item: Barcode?) { }
    override fun onDetectQrFromGallery(item: Barcode?) { }
    override fun onQrDetectError(message: String) { }
```

**Passo 4.**
Na função onCreate de sua atividade, configure os ouvintes de detector do QR e configure a visão da camera
```kotlin
    KambaQrReaderView.addDetectorListener(this)
    KambaQrReaderView.setupCameraView(this)
```
Você pode pausar e despausar a visão da câmera por exemplo:
```Kotlin
   override fun onPause() {
        super.onPause()
        if (cameraPermissionGranted) {
            KambaQrReaderView.pauseCameraPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        KambaQrReaderView.resumeCameraPreview()
    }

```
