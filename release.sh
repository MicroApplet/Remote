# 定义版本号生成规则（示例：当前版本为 1.0.0-SNAPSHOT，release 版本为 1.0.0，新快照版本为 1.0.1-SNAPSHOT）
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
NEW_VERSION=$(echo $RELEASE_VERSION | awk -F. '{$NF = $NF + 1; print $0}' | sed 's/ /./g')-SNAPSHOT

# 执行自动化发布
mvn clean install
mvn clean release:prepare -B -Prelease,!dev -DreleaseVersion=$RELEASE_VERSION -DdevelopmentVersion=$NEW_VERSION -DasialjimVersion=$RELEASE_VERSION
mvn install
mvn release:perform -B -DasialjimVersion=$RELEASE_VERSION