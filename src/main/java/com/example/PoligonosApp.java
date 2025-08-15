package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Uma aplicação desktop (usando a biblioteca <a href="http://openjfx.io"> OpenJFX (JavaFX)</a>)
 * que desenha polígonos na tela e calcula o perímetro de cada um:
 * a soma da distância entre todos os seus pontos.
 */
public class PoligonosApp extends Application {
    /**
     * Lista onde cada elemento representa os pontos que formam um polígono.
     * Cada elemento é então uma lista de pontos com coordenadas x,y.
     * Assim, cada polígono é formado por uma lista de pontos.
     */
    private final List<List<Point>> pontosPoligonos = List.of(
            // Quadrilátero (Quadrado)
            List.of(
                    new Point( 50,  50),
                    new Point(150,  50),
                    new Point(150, 150),
                    new Point( 50, 150)
            ),

            // Quadrilátero (Retângulo)
            List.of(
                    new Point(200,  50),
                    new Point(400,  50),
                    new Point(400, 100),
                    new Point(200, 100)
            ),

            // Triângulo
            List.of(
                    new Point(300, 250),
                    new Point(350, 150),
                    new Point(400, 250)
            ),

            // Pentágono
            List.of(
                    new Point(200, 250),
                    new Point(250, 300),
                    new Point(250, 350),
                    new Point(150, 350),
                    new Point(150, 300)
            ),

            // Hexágono
            List.of(
                    new Point(320, 270),
                    new Point(370, 320),
                    new Point(370, 370),
                    new Point(320, 420),
                    new Point(270, 370),
                    new Point(270, 320)
            )
    );

    /**
     * Executa a aplicação
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Inicia a apresentação da interface gráfica da aplicação.
     * @param mainStage janela inicial da aplicação
     */
    @Override
    public void start(final Stage mainStage) {
        final var root = new Pane();
        final var scene = new Scene(root, 800, 600);

        for (final var listaPontos : pontosPoligonos) {
            final var poligono = new Polygon();
            for (final Point point : listaPontos) {
                poligono.getPoints().addAll(point.x(), point.y());
            }

            poligono.setFill(Color.BLUE);
            poligono.setStroke(Color.BLACK);
            root.getChildren().add(poligono);
        }

        final List<String> perimetros = perimetros().stream().map(p -> String.format("%.1f", p)).toList();
        final var label1 = newLabel("Perímetro dos Polígonos: " + perimetros, 500);
        final var label2 = newLabel("Tipo dos Polígonos: " + tipoPoligonos(), 530);
        root.getChildren().addAll(label1, label2);

        mainStage.setTitle("Polígonos");
        mainStage.setScene(scene);
        mainStage.setAlwaysOnTop(true);
        mainStage.show();
    }

    private static Label newLabel(final String title, final int y) {
        final var label = new Label(title);
        label.setLayoutX(10);
        label.setLayoutY(y);
        return label;
    }

    /**
     * Descobre o tipo de cada polígono a partir da quantidade de pontos que ele tem.
     * Se um polígono representado por um elemento na lista {@link #pontosPoligonos} tiver 4 pontos,
     * ele é um "Quadrilátero", se tiver 3 é um "Triângulo" e assim por diante.
     *
     * <p>A implementação do método deve usar a operação {@link Stream#flatMap(Function)} que
     * percorre os itens de {@link #pontosPoligonos} (cada item representando um polígono).
     * O flatMap recebe então cada um destes itens, que é uma lista de pontos.
     * A partir de tal lista, deve obter o total de pontos.
     * Em uma operação posterior na cadeia de Stream, deve-se verificar
     * qual o total de pontos de cada item que representa um polígono
     * e devolver uma String indicando o tipo de polígono.</p>
     *
     * @return uma lista de String indicando se o polígono é um "quadrilátero" (quadrado ou retângulo),
     * "triângulo", "pentágono", "hexágono" ou apenas um "polígono" geral quando tiver mais de 6 lados.
     */
    protected List<String> tipoPoligonos() {
        return pontosPoligonos.stream()
                .map(pontos -> {
                    int numPontos = pontos.size();
                    return switch (numPontos) {
                        case 3 -> "Triângulo";
                        case 4 -> "Quadrilátero";
                        case 5 -> "Pentágono";
                        case 6 -> "Hexágono";
                        default -> "Polígono de " + numPontos + " lados";
                    };
                })
                .toList();
    }

    /**
     * Calcula o perímetro de cada polígono.
     * O perímetro é a soma da distância entre cada {@link Point} (x,y) do {@link Polygon}.
     * Se você pensar em um polígono como um quadrado, o perímetro representa a distância que você percorreria
     * se andasse ao redor da borda do quadrado, do ponto inicial até o último ponto.
     *
     * <p>Este método é mais complexo. A implementação dele deve usar a operação {@link Stream#flatMap(Function)} que
     * percorre os itens de {@link #pontosPoligonos} (cada item representando um polígono).
     * O que queremos obter de cada item (lista de pontos) é a soma da distância entre cada ponto.
     * </p>
     *
     * <p>O record {@link Point} (veja javadoc dele para mais detalhes)
     * possui um construtor {@link Point#Point(Point, Point)} que recebe 2 pontos,
     * cria um novo que contém:
     * (i) as coordenadas do segundo ponto e
     * (ii) a distância entre os pontos no atributo {@link Point#distance} (acessado pelo método getter {@link Point#distance()}).
     * Tal construtor já soma a distância entre p1 e p2 com a distância do p1 com o ponto anterior a ele.</p>
     *
     * <p>Assim, você precisaria percorrer todos os pontos de um polígono, pegar um par de pontos e passar
     * para tal construtor. Pegar o terceiro ponto e o ponto criado na chamada do construtor anteriormente,
     * e chamar o construtor novamente, passando estes 2 pontos, até que você chegue ao último ponto
     * do polígono.
     * Mas é exatamente isso que o reduce faz. Então para obter um ponto final contendo a soma da distância entre
     * todos os pontos de um polígono, você deve usar o método reduce no parâmetro recebido no flatMap.</p>
     *
     * <p>No entanto, considere que temos um triângulo. Se utilizamos o método {@link Stream#reduce(BinaryOperator)},
     * ele permitirá calcular a distância entre os pontos A -> B e B -> C somente.
     * Mas para calcular o perímetro, precisamos fechar os pontos, obtendo também a distância entre C -> A.
     * Assim, podemos começar do A (1o ponto) e indicar que o ponto anterior é o C.
     * Apesar de iniciar do A, estaríamos calculando as distâncias entre C -> A, A -> B e B -> C, fechando
     * todos os pontos. Com a versão do reduce indicada acima, não será possível fazer isso.
     * Desta forma, a versão {@link Stream#reduce(Object, BinaryOperator)} deve ser usada no lugar.
     * Leia o JavaDoc de tal método para mais detalhes.</p>
     *
     * <p>Após o flatMap, você vai ter um único ponto para cada polígono, que representa o último ponto encontrado
     * e contém o perímetro do polígono, que pode ser acessado por {@link Point#distance()}.
     * Desta forma, basta retornar este resultado como uma nova lista de {@link Double}.</p>
     *
     * @return uma lista contendo o perímetro de cada polígono
     */
    protected List<Double> perimetros() {
        return pontosPoligonos.stream()
                .map(pontos -> {
                    if (pontos.isEmpty()) return 0.0;

                    Point ultimoPonto = pontos.get(pontos.size() - 1);
                    Point primeiroPonto = pontos.get(0);

                    return pontos.stream()
                            .reduce(
                                    new Point(ultimoPonto, primeiroPonto),
                                    (p1, p2) -> new Point(p1, p2),
                                    (p1, p2) -> p2
                            )
                            .distance();
                })
                .toList();
    }
}
