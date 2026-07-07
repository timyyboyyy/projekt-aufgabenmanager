package de.iu.aufgabenmanager.dto;

/** Fortschritt eines Projekts anhand erledigter Aufgaben (US6). */
public record ProjectProgressResponse(
        Long projectId,
        long totalTasks,
        long doneTasks,
        int percentDone) {

    public static ProjectProgressResponse of(Long projectId, long totalTasks, long doneTasks) {
        int percent = totalTasks == 0
                ? 0
                : (int) Math.round(doneTasks * 100.0 / totalTasks);
        return new ProjectProgressResponse(projectId, totalTasks, doneTasks, percent);
    }
}
