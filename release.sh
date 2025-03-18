#!/bin/bash

#
#  Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#   limitations under the License.
#

# 定义版本号生成规则（示例：当前版本为 1.0.0-SNAPSHOT，release 版本为 1.0.0，新快照版本为 1.0.1-SNAPSHOT）
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
NEW_VERSION=$(echo $RELEASE_VERSION | awk -F. '{$NF = $NF + 1; print $0}' | sed 's/ /./g')-SNAPSHOT

# 执行自动化发布
mvn clean release:prepare -B \
  -DreleaseVersion=$RELEASE_VERSION \
  -DdevelopmentVersion=$NEW_VERSION \
  -Dtag=v$RELEASE_VERSION

mvn release:perform -B