package cn.xj.common.model;

import cn.xj.common.tool.IdWorker;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfo extends BaseBean {

    private Long id;
    private StringBuilder instruction;
    private Map params;
    private List<InstructionBuilder> instructionBuilders;
    private Object bean;
    private String className;
    private String node;
    private List queryResult;
    private Boolean modifyResult;
    private CacheInfo cacheInfo;

    @SuppressWarnings("unchecked")
    public void doIn(String mark, List<Param> list) {
        StringBuilder builder = new StringBuilder();
        if (list != null && list.size() - 1 > 0) {
            int len = list.size() - 1;
            IdWorker idWorker = new IdWorker(0, 0);
            Long id;
            builder.append("(");
            for (int i = 0; i < len; i++) {
                id = idWorker.nextId();
                builder.append("{").append(id).append("}");
                this.params.put("" + id, list.get(i));
            }
            id = idWorker.nextId();
            builder.append("{").append(id).append("})");
            this.params.put("" + id, list.get(len));
        }
        this.setInstruction(this.instruction.replace(this.instruction.indexOf(mark), this.instruction.indexOf(mark) + mark.length(), builder.toString()));
    }

    public void load() {
        String regex = "\\{[\\w|\\d]*\\}";
        String[] array = this.getInstruction().toString().split(regex);
        Pattern p = Pattern.compile("(\\{[\\w|\\d]*\\})");
        Matcher m = p.matcher(this.instruction.toString());
        String s, key;
        int i = 0;
        this.instructionBuilders = Lists.newArrayList();
        while (m.find()) {
            s = m.group();
            key = s.substring(1, s.length() - 1);
            if (this.params.get(key) == null) {
                this.instructionBuilders.add(InstructionBuilder.builder().handle(array[i] + "null").param(null).build());
            } else {
                this.instructionBuilders.add(InstructionBuilder.builder().handle(array[i]).param(this.params.get(key)).build());
            }
            i++;
        }
        if (i == array.length - 1) {
            this.instructionBuilders.add(InstructionBuilder.builder().handle(array[i]).param(null).build());
        }
    }

}
