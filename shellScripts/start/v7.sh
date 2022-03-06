if [ -z "$1" ]
then
      bash -c "screen -d -r v7 -X stuff $'ls\nsh autoRestart.sh\nstop\nhost\n'"
else
      bash -c "screen -d -r v7 -X stuff $'ls\nsh autoRestart.sh\nstop\nhost $1 $2\n'"
fi