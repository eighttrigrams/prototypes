#!/bin/bash

function provideSoftware {
	sudo rm -rf /nocloud > /dev/null
	sudo rm -rf /storage
	sudo mkdir /storage
	sudo mkdir /storage/devHost
	sudo mkdir /storage/`hostname`
	sudo chown -R vagrant:vagrant /storage
	sudo mkdir /nocloud
	sudo mkdir /nocloud/db
	sudo chown vagrant:vagrant /nocloud
	cp /vagrant/startWorker.sh /nocloud/startWorker.sh
	cp /vagrant/stopWorker.sh  /nocloud/stopWorker.sh
	cp -r /vagrant/lib /nocloud/
	mkdir -p /nocloud/db/foreign/localhost
	sudo chown -R vagrant:vagrant /nocloud
}


if [ -e /nocloud ]; then
	echo provisioning the software
	provideSoftware
	exit
fi
sudo mkdir /nocloud


## ssh password free access
a="ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA6NF8iallvQVp22WDkTkyrtvp9eWW6A8YVr+kz4TjGYe7gHzIw+niNltGEFHzD8+v1I2YJ6oXevct1YeS0o9HZyN1Q9qgCgzUFtdOKLv6IedplqoPkcmF0aYet2PkEDo3MlTBckFXPITAMzF8dJSIFo9D8HfdOV0IAdx4O7PtixWKn5y2hMNG0zQPyUecp4pzC6kivAIhyfHilFR61RGL+GPXQ2MWZWFYbAGjyiYJnAmCP3NOTd0jMZEnDkbUvxhMmBYSdETk1rRgm+R4LOzFUGaHqHDLKLX+FIPKcF96hrucXzcWyLbIbEgE98OHlnVYCzRdK8jlqm8tehUc9c9WhQ== vagrant insecure public key"
echo $a >> /home/vagrant/.ssh/authorized_keys

## ruby 2.2
sudo apt-get install curl -y
command curl -sSL https://rvm.io/mpapis.asc | gpg --import -
\curl -sSL https://get.rvm.io | bash -s stable --ruby
source /usr/local/rvm/scripts/rvm
echo "source /usr/local/rvm/scripts/rvm" >> /home/vagrant/.bashrc
rvm use ruby 2.2
sudo ln -sf /usr/share/zoneinfo/Europe/Amsterdam /etc/localtime




