# AWS ECR用のサンプルアプリケーション
## 概要
AWSインフラ構築時にECRイメージを多用するので、サンプルアプリケーションを作成し使用する際は本リポジトリを別名でクローンして使用する。

## 使用バージョン
| サービス | バージョン |
| ------------- | ------------- |
| Scala  | 2.13.3  |
| sbt  | 1.5.3  |
| sbt-plugin  | 2.7.5  |
| sbt-native-packager  | 1.7.6  |
| sbt-ecr  | 0.15.0  |
| sbt-release  | 1.0.13  |

## セットアップ手順
### 1. 本リポジトリを別名でクローンする。
```bash
$ git clone git@github.com:takapi327/sample-application-for-ecr-deployment.git ${your new application name}
```

### 2. gitの管理を解除する
```bash
$ rm -rf .git/
```

### 3. build.sbtの各所設定を変更(アプリケーション名の変更)
```
name := ${your new application name}

...

Docker / maintainer := ${your new AWS Account mail}

...

Ecr / repositoryName := ${your new application name}
```

### 4. 新しいリポジトリでGit管理を始める
```bash
$ git init
$ git add .
$ git commit -m "first commit"
$ git remote add origin git@github.com:${アカウント名}/${リポジトリ名}
$ git push -u origin master
```

### 5. AWS ECRへプッシュするためのシークレットキーの変更
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
