# TimerGateBall

Aplicativo Android simples que inicia uma contagem regressiva de 30 minutos com avisos sonoros e de voz em determinados momentos.

## Como executar


### Usando Android Studio
1. Abra o projeto no Android Studio.
2. Conecte um dispositivo ou inicie um emulador Android.
3. Clique em **Run** para compilar e instalar o aplicativo.

### Usando Docker
É possível compilar o APK utilizando o Dockerfile disponível no repositório.
Execute:
```bash
docker build -t timergateball .
```
O APK gerado ficará disponível na imagem em `/app-debug.apk`.
Você pode copiá-lo para o host com:
```bash
docker create --name tmp timergateball
docker cp tmp:/app-debug.apk ./app-debug.apk
docker rm tmp
```

## Estrutura
- `app/src/main` contém o código Kotlin, recursos e manifesto.
- `build.gradle.kts` e `settings.gradle.kts` definem a configuração do Gradle.

## Dependências
As dependências principais estão declaradas em `app/build.gradle.kts` e incluem AndroidX, Material Components e Android Media APIs.
