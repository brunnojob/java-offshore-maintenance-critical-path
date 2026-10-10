import java.io.*;
import java.util.*;
import java.util.stream.*;

public class Main {
    static double numeric(String value) {
        double v = Double.parseDouble(value);
        if (!Double.isFinite(v)) throw new IllegalArgumentException("finite number required");
        return v;
    }
    static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
    static List<String[]> input(int columns) throws IOException {
        var rows = new ArrayList<String[]>();
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            require(rows.size() < 100000, "input limit exceeded");
            var row = line.split(",", -1);
            require(row.length == columns && Arrays.stream(row).noneMatch(String::isBlank), "invalid CSV row");
            rows.add(row);
        }
        require(!rows.isEmpty(), "missing input");
        return rows;
    }
    public static void main(String[] args) {
        try {
            if (args.length == 1 && args[0].equals("--self-test")) { test(); System.out.println("tests passed"); }
            else run();
        } catch (Exception e) { System.err.println(e.getMessage()); System.exit(2); }
    }

    record Task(String id, double hours, List<String> dependencies) {}
    record Plan(List<String> order, Map<String, Double> start, Map<String, Double> finish, Map<String, Double> slack, double duration) {}
    static Plan plan(List<Task> tasks) {
        var byId = new LinkedHashMap<String, Task>();
        for (var t : tasks) { require(t.hours > 0, "positive duration required"); require(byId.putIfAbsent(t.id, t) == null, "duplicate task"); }
        var indegree = new HashMap<String, Integer>(); var children = new HashMap<String, List<String>>();
        for (var t : tasks) {
            require(new HashSet<>(t.dependencies).size() == t.dependencies.size(), "duplicate dependency");
            indegree.put(t.id, t.dependencies.size());
            for (var d : t.dependencies) { require(byId.containsKey(d), "unknown dependency"); children.computeIfAbsent(d, key -> new ArrayList<>()).add(t.id); }
        }
        var ready = new PriorityQueue<String>(); indegree.forEach((id, degree) -> { if (degree == 0) ready.add(id); });
        var order = new ArrayList<String>(); var start = new HashMap<String, Double>(); var finish = new HashMap<String, Double>();
        while (!ready.isEmpty()) {
            String id = ready.remove(); var t = byId.get(id);
            double s = t.dependencies.stream().mapToDouble(finish::get).max().orElse(0); start.put(id, s); finish.put(id, s + t.hours); order.add(id);
            for (var c : children.getOrDefault(id, List.of())) if (indegree.merge(c, -1, Integer::sum) == 0) ready.add(c);
        }
        require(order.size() == tasks.size(), "dependency cycle detected");
        double duration = finish.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        var latest = new HashMap<String, Double>(); var slack = new HashMap<String, Double>();
        for (int i = order.size() - 1; i >= 0; i--) {
            String id = order.get(i); double lf = children.getOrDefault(id, List.of()).stream().mapToDouble(latest::get).min().orElse(duration);
            latest.put(id, lf - byId.get(id).hours); slack.put(id, latest.get(id) - start.get(id));
        }
        return new Plan(order, start, finish, slack, duration);
    }
    static void test() {
        var p = plan(List.of(new Task("A", 2, List.of()), new Task("B", 3, List.of("A")), new Task("C", 1, List.of("A"))));
        require(p.duration == 5 && p.slack.get("C") == 2, "critical path mismatch");
        boolean rejected = false;
        try { plan(List.of(new Task("A", 1, List.of("B")), new Task("B", 1, List.of("A")))); } catch (IllegalArgumentException e) { rejected = true; }
        require(rejected, "cycle accepted");
    }
    static void run() throws IOException {
        var tasks = new ArrayList<Task>();
        for (var r : input(3)) tasks.add(new Task(r[0], numeric(r[1]), r[2].equals("-") ? List.of() : List.of(r[2].split("\\|"))));
        var p = plan(tasks);
        for (var id : p.order) System.out.printf(Locale.ROOT, "%s,start=%.2f,finish=%.2f,float=%.2f%n", id, p.start.get(id), p.finish.get(id), p.slack.get(id));
        System.out.printf(Locale.ROOT, "duration=%.2f%n", p.duration);
    }

}
