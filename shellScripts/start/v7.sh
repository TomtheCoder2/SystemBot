if [ -z "$1" ]
then
      bash -c "screen -d -r v7 -X stuff $'ls\njava -jar server-release.jar\nstop\nhost\n'"
else
      bash -c "screen -d -r v7 -X stuff $'ls\njava -jar server-release.jar\nstop\nhost $1 $2\n'"
fi