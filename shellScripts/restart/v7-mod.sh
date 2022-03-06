bash -c "screen -d -r v7-mod -X stuff $'save 1\n'"
sleep 1
bash -c "screen -d -r v7-mod -X stuff $'^C\n'"
sleep 1
bash -c "screen -d -r v7-mod -X stuff $'sh autoRestart.sh\nstop\nload 1\n'"
