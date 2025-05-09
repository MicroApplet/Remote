#!/bin/sh
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

# 定义应用组名
group_name='asialjim'
# 定义应用名称 ,这里的name是获取你仓库的名称，也可以自己写
app_name='remote-proxy'
docker_name='aj-remote-proxy'
# 定义应用版本
app_version='latest'
# 定义应用环境
profile_active='prod'
echo '...'
echo '发布网关开始...'
docker stop ${docker_name}

docker rm ${docker_name}

docker rmi ${group_name}/${app_name}:${app_version}

docker build -t ${group_name}/${app_name}:${app_version} .

docker run --name ${docker_name} \
--cpus="2" --memory="1g" \
--env-file /root/.env/mams.env \
-e 'spring.profiles.active'=${profile_active} \
-e TZ="Asia/Shanghai" \
-v /etc/localtime:/etc/localtime \
-v /home/asialjim/.app/docker/github/MicroBank/${app_name}/logs:/var/logs \
-d ${group_name}/${app_name}:${app_version}


echo '发布网关结束...'
