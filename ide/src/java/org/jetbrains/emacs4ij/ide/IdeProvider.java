package org.jetbrains.emacs4ij.ide;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.emacs4ij.ide.tool.EchoArea;
import org.jetbrains.emacs4ij.jelisp.GlobalEnvironment;
import org.jetbrains.emacs4ij.jelisp.LogUtil;
import org.jetbrains.emacs4ij.jelisp.exception.InternalException;
import org.jetbrains.emacs4ij.jelisp.platformDependent.Ide;

public class IdeProvider implements Ide {
  public IdeProvider () {}

  private EchoArea getEchoArea() {
    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
    MyProjectComponent myProjectComponent = openProjects[0].getComponent(MyProjectComponent.class);
    return myProjectComponent.getEchoArea();
  }

  @Override
  public void echo(final String message, @NotNull GlobalEnvironment.MessageType type) {
    TextAttributesKey outputKey;
    switch (type) {
      case INFO:
        outputKey = ConsoleViewContentType.NORMAL_OUTPUT_KEY;
        break;
      case WARNING:
        outputKey = ConsoleViewContentType.LOG_WARNING_OUTPUT_KEY;
        break;
      case ERROR:
        outputKey = ConsoleViewContentType.ERROR_OUTPUT_KEY;
        break;
      default:
        throw new InternalException(Emacs4ijBundle.message("unsupported.msg.type", type.toString()));
    }

    try {
      getEchoArea().print(message + "\n", outputKey);
    } catch (Exception e) {
      LogUtil.log(e);
    }
  }
}
