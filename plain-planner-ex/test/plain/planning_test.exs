defmodule Plain.PlanningTest do
  use Plain.DataCase

  alias Plain.Planning

  describe "tasks" do
    alias Plain.Planning.Task

    @valid_attrs %{name: "some name"}
    @update_attrs %{name: "some updated name"}
    @invalid_attrs %{name: nil}

    def task_fixture(attrs \\ %{}) do
      {:ok, task} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Planning.create_task()

      task
    end

    test "list_tasks/0 returns all tasks" do
      task = task_fixture()
      assert Planning.list_tasks() == [task]
    end

    test "get_task!/1 returns the task with given id" do
      task = task_fixture()
      assert Planning.get_task!(task.id) == task
    end

    test "create_task/1 with valid data creates a task" do
      assert {:ok, %Task{} = task} = Planning.create_task(@valid_attrs)
      assert task.name == "some name"
    end

    test "create_task/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Planning.create_task(@invalid_attrs)
    end

    test "update_task/2 with valid data updates the task" do
      task = task_fixture()
      assert {:ok, %Task{} = task} = Planning.update_task(task, @update_attrs)
      assert task.name == "some updated name"
    end

    test "update_task/2 with invalid data returns error changeset" do
      task = task_fixture()
      assert {:error, %Ecto.Changeset{}} = Planning.update_task(task, @invalid_attrs)
      assert task == Planning.get_task!(task.id)
    end

    test "delete_task/1 deletes the task" do
      task = task_fixture()
      assert {:ok, %Task{}} = Planning.delete_task(task)
      assert_raise Ecto.NoResultsError, fn -> Planning.get_task!(task.id) end
    end

    test "change_task/1 returns a task changeset" do
      task = task_fixture()
      assert %Ecto.Changeset{} = Planning.change_task(task)
    end
  end

  describe "goals" do
    alias Plain.Planning.Goal

    @valid_attrs %{name: "some name"}
    @update_attrs %{name: "some updated name"}
    @invalid_attrs %{name: nil}

    def goal_fixture(attrs \\ %{}) do
      {:ok, goal} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Planning.create_goal()

      goal
    end

    test "list_goals/0 returns all goals" do
      goal = goal_fixture()
      assert Planning.list_goals() == [goal]
    end

    test "get_goal!/1 returns the goal with given id" do
      goal = goal_fixture()
      assert Planning.get_goal!(goal.id) == goal
    end

    test "create_goal/1 with valid data creates a goal" do
      assert {:ok, %Goal{} = goal} = Planning.create_goal(@valid_attrs)
      assert goal.name == "some name"
    end

    test "create_goal/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Planning.create_goal(@invalid_attrs)
    end

    test "update_goal/2 with valid data updates the goal" do
      goal = goal_fixture()
      assert {:ok, %Goal{} = goal} = Planning.update_goal(goal, @update_attrs)
      assert goal.name == "some updated name"
    end

    test "update_goal/2 with invalid data returns error changeset" do
      goal = goal_fixture()
      assert {:error, %Ecto.Changeset{}} = Planning.update_goal(goal, @invalid_attrs)
      assert goal == Planning.get_goal!(goal.id)
    end

    test "delete_goal/1 deletes the goal" do
      goal = goal_fixture()
      assert {:ok, %Goal{}} = Planning.delete_goal(goal)
      assert_raise Ecto.NoResultsError, fn -> Planning.get_goal!(goal.id) end
    end

    test "change_goal/1 returns a goal changeset" do
      goal = goal_fixture()
      assert %Ecto.Changeset{} = Planning.change_goal(goal)
    end
  end
end
