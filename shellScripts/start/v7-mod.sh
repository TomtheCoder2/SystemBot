if [ -z "$1" ]
then
      bash -c "screen -r v7-mod -X stuff $'ls\njava -jar server-release.jar\nstop\nhost\n'"
else
      bash -c "screen -r v7-mod -X stuff $'ls\njava -jar server-release.jar\nstop\nhost $1 $2\n'"
fi