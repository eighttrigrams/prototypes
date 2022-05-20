defmodule PlainWeb.TaskController do
  use PlainWeb, :controller

  alias Plain.Planning
  alias Plain.Planning.Task

  def index(conn, %{"goal" => goal_id}) do
    goals = Planning.list_goals()
    tasks = Planning.list_tasks(goal_id)
    render(conn, "index.html", tasks: tasks, goals: goals)
  end

  def index(conn, _params) do
    goals = Planning.list_goals()
    tasks = Planning.list_tasks()
    render(conn, "index.html", tasks: tasks, goals: goals)
  end

  def new(conn, _params) do
    changeset = Planning.change_task(%Task{})
    render(conn, "new.html", changeset: changeset)
  end

  def create(conn, %{"task" => task_params}) do
    case Planning.create_task(task_params) do
      {:ok, task} ->
        conn
        |> put_flash(:info, "Task created successfully.")
        |> redirect(to: Routes.task_path(conn, :show, task))

      {:error, %Ecto.Changeset{} = changeset} ->
        render(conn, "new.html", changeset: changeset)
    end
  end

  def show(conn, %{"id" => id}) do
    task = Planning.get_task!(id)
    render(conn, "show.html", task: task)
  end

  def edit(conn, %{"id" => id}) do
    task = Planning.get_task!(id)
    changeset = Planning.change_task(task)
    render(conn, "edit.html", task: task, changeset: changeset)
  end

  def update(conn, %{"id" => id, "task" => task_params}) do
    task = Planning.get_task!(id)

    case Planning.update_task(task, task_params) do
      {:ok, task} ->
        conn
        |> put_flash(:info, "Task updated successfully.")
        |> redirect(to: Routes.task_path(conn, :show, task))

      {:error, %Ecto.Changeset{} = changeset} ->
        render(conn, "edit.html", task: task, changeset: changeset)
    end
  end

  def delete(conn, %{"id" => id}) do
    task = Planning.get_task!(id)
    {:ok, _task} = Planning.delete_task(task)

    conn
    |> put_flash(:info, "Task deleted successfully.")
    |> redirect(to: Routes.task_path(conn, :index))
  end
end
