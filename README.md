# kube-springboot-hazelcast
Demo project for use of Hazelcast within SpringBoot apps running in Kubernetes


`minikube --cpus=4 --disk-size="50g" --memory=4096 start` 

`eval $(minikube docker-env)`

`mvn clean package docker:build`

`kubectl create -f src/main/kubernetes/rc.yaml`

`kubectl create -f src/main/kubernetes/service.yaml`

`curl $(minikube ip):30001/health`

`curl $(minikube ip):30001/hazelcast/get`

`curl $(minikube ip):30001/hazelcast/add`

`curl $(minikube ip):30001/hazelcast/get`
