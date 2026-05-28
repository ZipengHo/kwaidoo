import gpf.adur.data.Form;

public class AntiPatternTableDataSystemField {

    public Long wrong(Form detail) throws Exception {
        return detail.getLong("排序序号");
    }
}
