#!/bin/sh

rm -f /etc/init.d/mci-background-jobs
rm -f /etc/default/mci-background-jobs
rm -f /var/run/mci-background-jobs

#Remove mci-background-jobs from chkconfig
chkconfig --del mci-background-jobs || true
