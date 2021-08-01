package fr.coudert.rendering;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.*;

import fr.coudert.maths.Vec3;

public class Shader {

	public static final Shader MAIN = new Shader("main.vert", "main.frag");
	public static final Shader SKYBOX = new Shader("skybox.vert", "skybox.frag");

	private int program;

	public Shader(String vertex, String fragment) {
		program = glCreateProgram();
		if(program == GL_FALSE) {
			System.err.println("Un erreur c'est produite lors du chargement d'un shader");
			System.exit(1);
		}
		addProgram(loadShader(vertex), GL_VERTEX_SHADER);
		addProgram(loadShader(fragment), GL_FRAGMENT_SHADER);
		compileShader();
	}

	private static String loadShader(String fileName) {
		StringBuilder shaderSource = new StringBuilder("");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./res/sha/" + fileName));
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return shaderSource.toString();
	}

	private void addProgram(String text, int type) {
		int shader = glCreateShader(type);
		if(shader == GL_FALSE) {
			System.err.println("Erreur lors de la création d'un shader (" + shader + ")");
			System.exit(1);
		}
		glShaderSource(shader, text);
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(shader, 2048));
			System.exit(1);
		}
		glAttachShader(program, shader);
	}

	private void compileShader() {
		glLinkProgram(program);
		if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(program, 2048));
			System.exit(1);
		}
		glValidateProgram(program);
		if(glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(program, 2048));
			System.exit(1);
		}
	}

	public void bind() {
		glUseProgram(program);
	}

	public static void unBind() {
		glUseProgram(0);
	}

	public void setUniform(String name, int value) {
		glUniform1i(glGetUniformLocation(program, name), value);
	}

	public void setUniform(String name, float value) {
		glUniform1f(glGetUniformLocation(program, name), value);
	}

	public void setUniform(String name, Vec3 value) {
		glUniform3f(glGetUniformLocation(program, name), value.x, value.y, value.z);
	}
	
}