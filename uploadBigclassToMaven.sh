#!/usr/bin/env bash

## 自动批量发布脚本（需配合对应的build.gradle）
## 【说明】
## 必须严格按照以下顺序发布到Maven
## 执行本脚本前，必须先修改 version.cfg 文件，配置发版版本号。

./bigclass/uploadToMaven.sh