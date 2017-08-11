echo "Pushing service docker images to docker hub ...."
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker push bilalwahla/df-zipkinsvr:$BUILD_NAME
docker push bilalwahla/df-licensing-service:$BUILD_NAME
docker push bilalwahla/df-organization-service:$BUILD_NAME
docker push bilalwahla/df-confsvr:$BUILD_NAME
docker push bilalwahla/df-eurekasvr:$BUILD_NAME
docker push bilalwahla/df-zuulsvr:$BUILD_NAME
