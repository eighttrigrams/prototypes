# Script for populating the database. You can run it as:
#
#     mix run priv/repo/seeds.exs
#
# Inside the script, you can read and write to any of your
# repositories directly:
#
#     Plain.Repo.insert!(%Plain.SomeSchema{})
#
# We recommend using the bang functions (`insert!`, `update!`
# and so on) as they will fail if something goes wrong.

alias Plain.Repo
alias Plain.Accounts.User
alias Plain.Planning.{Goal, Task}

goal1  = %Goal{name: "G1", tasks: [%Task{name: "1a"}, %Task{name: "1b"}]}
goal2  = %Goal{name: "G2", tasks: [%Task{name: "2a"}, %Task{name: "2b"}]}

Repo.insert!(goal1)
Repo.insert!(goal2)

userA = %User{name: "Tobi"}
userB = %User{name: "Daniel"}

userA = Repo.insert!(userA)
userB = Repo.insert!(userB)

task1a = Repo.get_by(Task, name: "1a")
task1a = Repo.preload(task1a, [:goal, :users])
task1b = Repo.get_by(Task, name: "1b")
task1b = Repo.preload(task1b, [:goal, :users])
task2a = Repo.get_by(Task, name: "2a")
task2a = Repo.preload(task2a, [:goal, :users])
task2b = Repo.get_by(Task, name: "2b")
task2b = Repo.preload(task2b, [:goal, :users])

task_1a_changeset = Ecto.Changeset.change(task1a)
task_1b_changeset = Ecto.Changeset.change(task1b)
task_2a_changeset = Ecto.Changeset.change(task2a)
task_2b_changeset = Ecto.Changeset.change(task2b)

task_users_changeset_1a = Ecto.Changeset.put_assoc(task_1a_changeset, :users, [userA])
task_users_changeset_1b = Ecto.Changeset.put_assoc(task_1b_changeset, :users, [userB])
task_users_changeset_2a = Ecto.Changeset.put_assoc(task_2a_changeset, :users, [userA])
task_users_changeset_2b = Ecto.Changeset.put_assoc(task_2b_changeset, :users, [userB])

Repo.update!(task_users_changeset_1a)
Repo.update!(task_users_changeset_1b)
Repo.update!(task_users_changeset_2a)
Repo.update!(task_users_changeset_2b)




