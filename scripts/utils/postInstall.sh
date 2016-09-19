#!/bin/sh

ln -s /opt/mci-background-jobs/bin/mci-background-jobs /etc/init.d/mci-background-jobs
ln -s /opt/mci-background-jobs/etc/mci-background-jobs /etc/default/mci-background-jobs
ln -s /opt/mci-background-jobs/var /var/run/mci-background-jobs

if [ ! -e /var/log/mci-background-jobs ]; then
    mkdir /var/log/mci-background-jobs
fi

# Add mci-background-jobs service to chkconfig
chkconfig --add mci-background-jobs