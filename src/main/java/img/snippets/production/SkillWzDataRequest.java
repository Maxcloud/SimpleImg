package img.snippets.production;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class SkillWzDataRequest implements ImplWzDataRequest {

    private final int skillId;
    private final Path filePath;

    public SkillWzDataRequest(int skillId) {
        String wzFilePath = System.getProperty("wz.path");
        Path imgFilePath = Path.of(wzFilePath);

        this.skillId    = skillId;
        int classId     = skillId / 10000;

        String imgPath  = "Skill.wz/%d.img".formatted(classId);
        this.filePath   = imgFilePath.resolve(imgPath);
    }

    @Override
    public String getImgPath() {
        return "skill/" + skillId;
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

}
