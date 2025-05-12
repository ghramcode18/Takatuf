sudo apt-get update && sudo apt-get install -y apt-transport-https ca-certificates 
curl sudo curl -fsSL https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo bash -c 'cat <<EOF >/etc/apt/sources.list.d/kubernetes.list
deb https://apt.kubernetes.io/ kubernetes-xenial main
EOF'
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl

nameserver 10.15.1.2
nameserver 10.11.12.2
nameserver 103.86.99.100
nameserver 1.1.1.1
nameserver 8.8.8.8
nameserver 8.8.4.4


kubectl apply -f namespace.yaml
kubectl apply -f backend-config.yaml
kubectl apply -f backend-secret.yaml
kubectl apply -f postgres-secret.yaml
kubectl apply -f nginx-config.yaml
kubectl apply -f postgres-service.yaml
kubectl apply -f postgres-statefulset.yaml
kubectl apply -f takatuf-service.yaml
kubectl apply -f takatuf-deployment.yaml
kubectl apply -f nginx-service.yaml
kubectl apply -f nginx-deployment.yaml
