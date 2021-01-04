defmodule Plain.Repo.Migrations.AddGoalIdToTasks do
  use Ecto.Migration

  def change do
    alter table(:tasks) do
      add :goal_id, references(:goals)
    end
  end
end
