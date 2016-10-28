# kube-springboot-hazelcast
Demo project for use of Hazelcast within SpringBoot apps running in Kubernetes


`minikube --cpus=4 --disk-size="50g" --memory=4096 start` 

`eval $(minikube docker-env)`

`mvn clean package docker:build`

`kubectl create -f src/main/kubernetes/rc.yaml`
