if [ -z "$1" ]
then
      bash -c "screen -r v7-mod -X stuff $'ls\nsh autoRestart.sh\nstop\nhost\n'"
else
      bash -c "screen -r v7-mod -X stuff $'ls\nsh autoRestart.sh\nstop\nhost $1 $2\n'"
fi