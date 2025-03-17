#!/bin/bash

# 定义版本号生成规则（示例：当前版本为 1.0.0-SNAPSHOT，release 版本为 1.0.0，新快照版本为 1.0.1-SNAPSHOT）
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
NEW_VERSION=$(echo $RELEASE_VERSION | awk -F. '{$NF = $NF + 1; print $0}' | sed 's/ /./g')-SNAPSHOT

# 执行自动化发布
mvn release:prepare -B \
  -DreleaseVersion=$RELEASE_VERSION \
  -DdevelopmentVersion=$NEW_VERSION \
  -Dtag=v$RELEASE_VERSION

mvn release:perform -B

# 推送到 Git 仓库（确保 Docker 容器有权限）
git push origin master --tags
