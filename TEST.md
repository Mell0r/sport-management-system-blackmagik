Process applications:
test-data/whole-tests/competition-configuration -o out/ start -a test-data/whole-tests/small-test-1/applications

To get results:
test-data/whole-tests/competition-configuration -o out/results result -p out/participant-list/participants-list.csv -s out/starting-protocols -tp of_participant -r test-data/whole-tests/small-test-1/checkpoint-completion-protocols-by-participants

To get teams result:
test-data/whole-tests/competition-configuration -o out/result_teams result_teams -p out/participant-list/participants-list.csv -r out/results



